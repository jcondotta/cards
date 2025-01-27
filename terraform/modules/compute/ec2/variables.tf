variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "current_aws_account_id" {
  description = "The current AWS account ID"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "subnet_ids" {
  description = "The IDs of the subnets the ec2 instances will be placed"
  type        = list(string)
}

variable "allowed_security_group_ids" {
  description = "The list of security group IDs that are allowed to access the EC2 instances"
  type        = list(string)
}

variable "dynamodb_cards_table_arn" {
  description = "The ARN of the DynamoDB cards table the Lambda will interact with"
  type        = string
}