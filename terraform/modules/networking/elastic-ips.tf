resource "aws_eip" "nat_elastic_ips" {
  for_each = aws_subnet.public_subnets

  tags = {
    Name = "cards-nat-eip-${each.key}"
  }
}