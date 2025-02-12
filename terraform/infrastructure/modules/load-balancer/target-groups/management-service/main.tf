locals {
  lb_tg_card_management = "lb-tg-card-management"
}

resource "aws_lb_target_group" "this" {
  name        = local.lb_tg_card_management
  port        = var.lb_target_group_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    path                = "/health"
    interval            = 15
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }

  tags = {
    Name = local.lb_tg_card_management
  }
}
resource "aws_lb_listener_rule" "card_management_listener_rule" {
  listener_arn = var.listener_http_arn
  priority     = 100

  condition {
    path_pattern {
      values = ["/api/v1/cards", "/api/v1/cards/*"]
    }
  }

  action {
    type = "forward"
    forward {
      target_group {
        arn = aws_lb_target_group.this.arn
      }
    }
  }

  tags = {
    Name = "card-management-listener-http-rule"
  }
}
