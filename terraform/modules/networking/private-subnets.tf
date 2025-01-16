resource "aws_subnet" "private_subnets" {
  for_each = {
    "us-east-1a" = "10.0.0.0/26",
    "us-east-1b" = "10.0.0.64/26"
  }

  vpc_id                   = aws_vpc.this.id
  cidr_block               = each.value
  availability_zone        = each.key
  map_public_ip_on_launch  = false

  tags = {
    Name = "cards-private-subnet-${each.key}"
  }
}