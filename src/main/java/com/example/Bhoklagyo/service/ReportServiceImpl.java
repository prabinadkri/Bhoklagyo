package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.DashboardReportResponse;
import com.example.Bhoklagyo.dto.ReportTopItem;
import com.example.Bhoklagyo.dto.RevenuePoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EntityManager em;

    public ReportServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public DashboardReportResponse getDashboardReport(Long restaurantId, LocalDate startDate, LocalDate endDate, String interval) {
        // Convert dates to timestamp range (inclusive)
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay(); // exclusive end

        Double totalRevenue = fetchTotalRevenue(restaurantId, start, end);
        Long totalOrders = fetchTotalOrders(restaurantId, start, end);
        List<ReportTopItem> topItems = fetchTopItems(restaurantId, start, end);
        List<RevenuePoint> revenueTrend = fetchRevenueTrend(restaurantId, start, end, interval);

        return new DashboardReportResponse(totalRevenue, totalOrders, topItems, revenueTrend);
    }

    private Double fetchTotalRevenue(Long restaurantId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COALESCE(SUM(oi.price_at_order * oi.quantity),0) FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.restaurant_id = :rid AND o.order_time >= :start AND o.order_time < :end AND o.status IN ('DELIVERED', 'COMPLETED')";
        Query q = em.createNativeQuery(sql);
        q.setParameter("rid", restaurantId);
        q.setParameter("start", start);
        q.setParameter("end", end);
        Object single = q.getSingleResult();
        if (single == null) return 0.0;
        return ((Number) single).doubleValue();
    }

    private Long fetchTotalOrders(Long restaurantId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.restaurant_id = :rid AND o.order_time >= :start AND o.order_time < :end and o.status IN ('DELIVERED', 'COMPLETED')";
        Query q = em.createNativeQuery(sql);
        q.setParameter("rid", restaurantId);
        q.setParameter("start", start);
        q.setParameter("end", end);
        Object single = q.getSingleResult();
        return ((Number) single).longValue();
    }

    private List<ReportTopItem> fetchTopItems(Long restaurantId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT rmi.name, SUM(oi.quantity) as qty FROM order_items oi JOIN restaurant_menu_items rmi ON oi.restaurant_menu_item_id = rmi.id JOIN orders o ON oi.order_id = o.id WHERE o.restaurant_id = :rid AND o.order_time >= :start AND o.order_time < :end GROUP BY rmi.name ORDER BY qty DESC LIMIT 5";
        Query q = em.createNativeQuery(sql);
        q.setParameter("rid", restaurantId);
        q.setParameter("start", start);
        q.setParameter("end", end);
        List<Object[]> results = q.getResultList();
        List<ReportTopItem> out = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[0];
            Number qty = (Number) row[1];
            out.add(new ReportTopItem(name, qty == null ? 0L : qty.longValue()));
        }
        return out;
    }

    private List<RevenuePoint> fetchRevenueTrend(Long restaurantId, LocalDateTime start, LocalDateTime end, String interval) {
        String trunc;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("weekly".equalsIgnoreCase(interval)) {
            trunc = "week";
        } else if ("monthly".equalsIgnoreCase(interval)) {
            trunc = "month";
        } else if ("yearly".equalsIgnoreCase(interval) || "year".equalsIgnoreCase(interval)) {
            trunc = "year";
        } else {
            // default daily
            trunc = "day";
        }

        String sql = "SELECT date_trunc('" + trunc + "', o.order_time) as period, COALESCE(SUM(oi.price_at_order * oi.quantity),0) as revenue FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.restaurant_id = :rid AND o.order_time >= :start AND o.order_time < :end AND o.status IN ('DELIVERED', 'COMPLETED') GROUP BY period ORDER BY period";
        Query q = em.createNativeQuery(sql);
        q.setParameter("rid", restaurantId);
        q.setParameter("start", start);
        q.setParameter("end", end);
        List<Object[]> results = q.getResultList();
        List<RevenuePoint> out = new ArrayList<>();
        for (Object[] row : results) {
            java.sql.Timestamp ts = (java.sql.Timestamp) row[0];
            Number revenue = (Number) row[1];
            String periodStr = ts.toLocalDateTime().toLocalDate().format(fmt);
            out.add(new RevenuePoint(periodStr, revenue == null ? 0.0 : revenue.doubleValue()));
        }
        return out;
    }
}
