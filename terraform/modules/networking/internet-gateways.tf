resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id

  tags = {
    Name = "cards-internet-gateway-${var.environment}"
  }
}