# resource "aws_vpc_endpoint" "dynamodb_gateway_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.dynamodb"
#   vpc_endpoint_type = "Gateway"
#
#   route_table_ids = [
#     aws_route_table.private_route_table.id
#   ]
#
#   tags = {
#     Name = "dynamodb-gateway-vpc-endpoint-${var.environment}"
#   }
# }
#
# resource "aws_vpc_endpoint" "sqs_vpc_endpoint" {
#   vpc_id             = aws_vpc.this.id
#   service_name       = "com.amazonaws.${var.aws_region}.sqs"
#   vpc_endpoint_type  = "Interface"
#   subnet_ids         = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.sqs_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "sqs-vpc-endpoint-${var.environment}"
#   }
# }
# #
# resource "aws_vpc_endpoint" "ecr_api_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.ecr.api"
#   vpc_endpoint_type = "Interface"
#   subnet_ids        = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.ecr_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "ecr-vpc-endpoint-${var.environment}"
#   }
# }
#
# resource "aws_vpc_endpoint" "ecr_dkr_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.ecr.dkr"
#   vpc_endpoint_type = "Interface"
#   subnet_ids        = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.ecr_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "ecr-dkr-vpc-endpoint-${var.environment}"
#   }
# }
#
# resource "aws_vpc_endpoint" "s3_gateway_vpc_endpoint" {
#   vpc_id            = aws_vpc.this.id
#   service_name      = "com.amazonaws.${var.aws_region}.s3"
#   vpc_endpoint_type = "Gateway"
#
#   route_table_ids = [
#     aws_route_table.private_route_table.id
#   ]
#
#   tags = {
#     Name = "s3-gateway-vpc-endpoint-${var.environment}"
#   }
# }
#
# resource "aws_vpc_endpoint" "cloudwatch_vpc_endpoint" {
#   vpc_id             = aws_vpc.this.id
#   service_name       = "com.amazonaws.${var.aws_region}.logs"
#   vpc_endpoint_type  = "Interface"
#   subnet_ids         = values(aws_subnet.private_subnets)[*].id
#   private_dns_enabled = true
#
#   security_group_ids = [
#     aws_security_group.cloudwatch_logs_vpc_endpoint_sg.id
#   ]
#
#   tags = {
#     Name = "cloudwatch-logs-vpc-endpoint-${var.environment}"
#   }
# }