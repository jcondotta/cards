locals {
  redis_sg_name = "cache-sg-${var.environment}"
}

resource "aws_security_group" "cache_security_group" {
  name        = local.redis_sg_name
  description = "Security group for Cache Cluster"

  vpc_id = var.vpc_id

  ingress {
    description = "Allow inbound Redis traffic (port 6379) from ECS services and Lambda."
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    security_groups = var.security_group_ids
  }

  tags = {
    Name = local.redis_sg_name
  }
}