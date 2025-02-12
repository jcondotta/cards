locals {
  process_cards_lambda_sg_name = "process-cards-lambda-sg-${var.environment}"
}

resource "aws_security_group" "process_cards_lambda_sg" {
  name        = local.process_cards_lambda_sg_name
  description = "Security group for the Lambda function, restricting inbound traffic to resources within the VPC."

  vpc_id      = var.vpc_id

  egress {
    description = "Allow outbound traffic only within the VPC."
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.process_cards_lambda_sg_name
  }
}