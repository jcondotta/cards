variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "public_subnet_ids" {
  description = "The IDs of the public subnets"
  type        = list(string)
}