resource "aws_lb" "this" {
  name               = "cards-load-balancer"
  load_balancer_type = "application"

  subnets = var.public_subnet_ids

  security_groups = [
    aws_security_group.load_balancer_security_group.id
  ]

  tags = {
    Name = "cards-load-balancer"
  }
}