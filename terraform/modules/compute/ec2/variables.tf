variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "public_subnet_ids" {
  description = "The IDs of the public subnets"
  type        = list(string)
}

variable "allowed_security_group_ids" {
  description = "The list of security group IDs that are allowed to access the EC2 instances"
  type        = list(string)
}