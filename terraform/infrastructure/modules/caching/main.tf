resource "aws_elasticache_replication_group" "cache_cluster" {
  replication_group_id = var.cache_cluster_name
  description          = "Cache cluster for cards-${var.environment}"

  engine                  = "valkey"
  engine_version          = "7.2"
  parameter_group_name    = "default.valkey7"
  node_type               = "cache.t4g.micro"
  num_node_groups         = 1
  replicas_per_node_group = 1

  security_group_ids = [
    aws_security_group.cache_security_group.id
  ]

  port                       = 6379
  automatic_failover_enabled = true
  subnet_group_name          = aws_elasticache_subnet_group.cache_subnet_group.name

  tags = {
    Name = var.cache_cluster_name
  }
}


resource "aws_elasticache_subnet_group" "cache_subnet_group" {
  name       = "cache-subnet-group-${var.environment}"
  subnet_ids = var.subnet_ids

  description = "Subnet group for cards cache cluster"
}