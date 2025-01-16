resource "aws_security_group" "load_balancer_security_group" {
  name        = "cards-load-balancer-security-group"
  description = "Security group for the Application Load Balancer (ALB)"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow inbound HTTP traffic from all sources"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic from the ALB"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "cards-load-balancer-security-group"
  }
}