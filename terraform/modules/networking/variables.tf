variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
  default = {}
}