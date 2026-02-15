#!/bin/bash
# ============================================================
# Bhoklagyo Kubernetes Deployment Script
# ============================================================
# Usage:
#   ./k8s/deploy.sh                  # Deploy everything
#   ./k8s/deploy.sh --dry-run        # Preview without applying
#   ./k8s/deploy.sh --delete         # Tear down
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NAMESPACE="bhoklagyo"
DRY_RUN=""
ACTION="apply"

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --dry-run)
      DRY_RUN="--dry-run=client"
      echo "=== DRY RUN MODE ==="
      shift
      ;;
    --delete)
      ACTION="delete"
      echo "=== DELETE MODE ==="
      shift
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

echo ""
echo "Bhoklagyo K8s Deployment"
echo "========================"
echo "Action:    $ACTION"
echo "Namespace: $NAMESPACE"
echo ""

# Apply in dependency order
MANIFESTS=(
  "namespace.yml"
  "secret.yml"
  "configmap.yml"
  "postgres.yml"
  "redis.yml"
  "kafka.yml"
  "app-deployment.yml"
  "hpa.yml"
  "pdb.yml"
  "ingress.yml"
  "network-policy.yml"
  "monitoring.yml"
)

if [ "$ACTION" == "delete" ]; then
  # Delete in reverse order
  for (( i=${#MANIFESTS[@]}-1; i>=0; i-- )); do
    manifest="${MANIFESTS[$i]}"
    echo "--- Deleting: $manifest"
    kubectl delete -f "$SCRIPT_DIR/$manifest" --ignore-not-found $DRY_RUN || true
  done
  echo ""
  echo "Teardown complete."
else
  for manifest in "${MANIFESTS[@]}"; do
    echo "--- Applying: $manifest"
    kubectl apply -f "$SCRIPT_DIR/$manifest" $DRY_RUN
  done

  echo ""
  echo "Deployment complete!"
  echo ""
  echo "Useful commands:"
  echo "  kubectl get pods -n $NAMESPACE"
  echo "  kubectl logs -f deployment/bhoklagyo-app -n $NAMESPACE"
  echo "  kubectl get hpa -n $NAMESPACE"
  echo "  kubectl port-forward svc/bhoklagyo-app 8080:80 -n $NAMESPACE"
  echo "  kubectl port-forward svc/prometheus 9090:9090 -n $NAMESPACE"
fi
