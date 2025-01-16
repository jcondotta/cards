resource "aws_route_table" "private_router_tables" {
  for_each = aws_subnet.private_subnets

  vpc_id = aws_vpc.this.id

  tags = {
    Name = "cards-private-router-table-${each.key}"
  }
}

resource "aws_route_table_association" "private_subnet_associations" {
  for_each = aws_subnet.private_subnets

  subnet_id      = each.value.id
  route_table_id = aws_route_table.private_router_tables[each.key].id
}