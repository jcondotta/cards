resource "aws_vpc_endpoint" "dynamodb_endpoint" {
  vpc_id            = aws_vpc.this.id
  service_name      = "com.amazonaws.${var.aws_region}.dynamodb"
  vpc_endpoint_type = "Gateway"

  route_table_ids = [for route_table in aws_route_table.private_route_tables : route_table.id]

  tags = {
    Name = "cards-dynamodb-vpc-endpoint"
  }
}