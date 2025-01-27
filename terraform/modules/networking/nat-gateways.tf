resource "aws_nat_gateway" "nat_gateways" {
  for_each      = aws_eip.nat_elastic_ips
  allocation_id = each.value.id
  subnet_id     = aws_subnet.public_subnets[each.key].id

  tags = {
    Name = "cards-nat-gateway-${each.key}"
  }
}