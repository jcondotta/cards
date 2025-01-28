resource "aws_security_group" "ecs_task_security_group" {
  name_prefix = "cards-ecs-task-security-group"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 8072
    to_port     = 8072
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}