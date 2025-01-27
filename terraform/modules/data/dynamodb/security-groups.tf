# resource "aws_security_group" "dynamodb_vpce_security_group" {
#   name        = "dynamodb-vpce-security-group"
#   description = "Allow access to DynamoDB VPC Endpoint"
#   vpc_id      = var.vpc_id
#
#   ingress {
#     description = "Allow HTTPS traffic from allowed security groups"
#     from_port   = 443
#     to_port     = 443
#     protocol    = "tcp"
#     security_groups = var.allowed_security_group_ids
#   }
#
#   tags = {
#     Name = "dynamodb-vpce-security-group"
#   }
# }