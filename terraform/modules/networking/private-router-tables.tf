resource "aws_route_table" "private_route_tables" {
  for_each = aws_subnet.private_subnets

  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gateways[each.key].id
  }

  tags = {
    Name = "cards-private-route-table-${each.key}"
  }
}

resource "aws_route_table_association" "private_subnet_associations" {
  for_each = aws_subnet.private_subnets

  subnet_id      = each.value.id
  route_table_id = aws_route_table.private_route_tables[each.key].id
}
