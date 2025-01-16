resource "aws_lb_target_group" "cards_load_balancer_target_group" {
  name        = "cards-load-balancer-target-group"
  port        = 8073
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "instance" # Targets EC2 instances

  health_check {
    protocol = "HTTP"
    port     = "8073"
    path     = "/health"
    matcher  = "200-399"
    interval = 30
  }

  tags = {
    Name = "cards-load-balancer-target-group"
  }
}

resource "aws_lb_target_group_attachment" "cards_tg_attachment" {
  for_each         = tomap({ for idx, id in var.ec2_instance_ids : idx => id })
  target_group_arn = aws_lb_target_group.cards_load_balancer_target_group.arn
  target_id        = each.value
  port             = 8073
}