resource "aws_subnet" "public_subnets" {
  for_each = {
    "us-east-1a" = "10.0.0.0/28",
    "us-east-1b" = "10.0.0.16/28"
    "us-east-1c" = "10.0.0.32/28"
  }

  vpc_id                   = aws_vpc.this.id
  cidr_block               = each.value
  availability_zone        = each.key
  map_public_ip_on_launch  = true

  tags = {
    Name = "cards-public-subnet-${each.key}"
  }
}