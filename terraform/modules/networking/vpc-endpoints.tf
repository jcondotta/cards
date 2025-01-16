resource "aws_vpc_endpoint" "dynamodb_endpoint" {
  vpc_id            = aws_vpc.this.id
  service_name      = "com.amazonaws.${var.aws_region}.dynamodb"
  vpc_endpoint_type = "Gateway"

  route_table_ids = [for router_table in aws_route_table.private_router_tables : router_table.id]

  tags = {
    Name = "cards-dynamodb-vpc-endpoint"
  }
}