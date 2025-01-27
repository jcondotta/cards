resource "aws_security_group" "ec2_cards_security_group" {
  name        = "ec2-cards-security-group"
  description = "Allow SSH and HTTP/HTTPS access to the EC2 instance"
  vpc_id      = var.vpc_id

  ingress {
    description = "Allow traffic from Load Balancer on port 8073"
    from_port   = 8073
    to_port     = 8073
    protocol    = "tcp"
    security_groups = var.allowed_security_group_ids
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "ec2-cards-security-group"
  }
}