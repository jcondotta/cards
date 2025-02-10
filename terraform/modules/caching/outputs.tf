output "redis_host" {
  description = "The primary Redis host (without port)"
  value       = split(":", aws_elasticache_replication_group.cache_cluster.primary_endpoint_address)[0]
}

output "redis_port" {
  description = "The Redis port"
  value       = aws_elasticache_replication_group.cache_cluster.port
}

output "redis_reader_host" {
  description = "The read replica host (without port)"
  value       = split(":", aws_elasticache_replication_group.cache_cluster.reader_endpoint_address)[0]
}