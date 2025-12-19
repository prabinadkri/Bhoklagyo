package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.AdminReportResponse;
import com.example.Bhoklagyo.dto.ReportTopRestaurant;
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
public class AdminReportServiceImpl implements AdminReportService {

    private final EntityManager em;

    public AdminReportServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public AdminReportResponse getAdminReport(LocalDate startDate, LocalDate endDate, String interval, int limit) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Double totalRevenue = fetchTotalRevenue(start, end);
        Long totalOrders = fetchTotalOrders(start, end);
        List<ReportTopRestaurant> topRestaurants = fetchTopRestaurants(start, end, limit);
        List<RevenuePoint> revenueTrend = fetchRevenueTrend(start, end, interval);

        return new AdminReportResponse(totalRevenue, totalOrders, topRestaurants, revenueTrend);
    }

    private Double fetchTotalRevenue(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COALESCE(SUM(oi.price_at_order * oi.quantity),0) FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.order_time >= :start AND o.order_time < :end AND o.status = 'DELIVERED'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("start", start);
        q.setParameter("end", end);
        Object single = q.getSingleResult();
        if (single == null) return 0.0;
        return ((Number) single).doubleValue();
    }

    private Long fetchTotalOrders(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.order_time >= :start AND o.order_time < :end AND o.status = 'DELIVERED'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("start", start);
        q.setParameter("end", end);
        Object single = q.getSingleResult();
        return ((Number) single).longValue();
    }

    private List<ReportTopRestaurant> fetchTopRestaurants(LocalDateTime start, LocalDateTime end, int limit) {
        String sql = "SELECT r.name, COALESCE(SUM(oi.price_at_order * oi.quantity),0) as revenue, COUNT(DISTINCT o.id) as orders FROM order_items oi JOIN orders o ON oi.order_id = o.id JOIN restaurants r ON o.restaurant_id = r.id WHERE o.order_time >= :start AND o.order_time < :end AND o.status = 'DELIVERED' GROUP BY r.id, r.name ORDER BY revenue DESC";
        Query q = em.createNativeQuery(sql);
        q.setParameter("start", start);
        q.setParameter("end", end);
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        List<Object[]> results = q.getResultList();
        List<ReportTopRestaurant> out = new ArrayList<>();
        for (Object[] row : results) {
            String name = (String) row[0];
            Number revenue = (Number) row[1];
            Number orders = (Number) row[2];
            out.add(new ReportTopRestaurant(name, revenue == null ? 0.0 : revenue.doubleValue(), orders == null ? 0L : orders.longValue()));
        }
        return out;
    }

    private List<RevenuePoint> fetchRevenueTrend(LocalDateTime start, LocalDateTime end, String interval) {
        String trunc;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if ("weekly".equalsIgnoreCase(interval)) {
            trunc = "week";
        } else if ("monthly".equalsIgnoreCase(interval)) {
            trunc = "month";
        } else if ("yearly".equalsIgnoreCase(interval) || "year".equalsIgnoreCase(interval)) {
            trunc = "year";
        } else {
            trunc = "day";
        }

        String sql = "SELECT date_trunc('" + trunc + "', o.order_time) as period, COALESCE(SUM(oi.price_at_order * oi.quantity),0) as revenue FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.order_time >= :start AND o.order_time < :end AND o.status = 'DELIVERED' GROUP BY period ORDER BY period";
        Query q = em.createNativeQuery(sql);
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
