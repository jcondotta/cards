variable "environment" {
  description = "Deployment environment (e.g., dev, prod)"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "cache_cluster_name" {
  description = "Name of the cache cluster"
  type        = string
}

variable "security_group_ids" {
  description = "List of security group IDs for the cache cluster"
  type        = list(string)
}

variable "subnet_ids" {
  description = "List of subnet IDs for the cache cluster"
  type        = list(string)
}