locals {
  lb_tg_card_query = "lb-tg-card-query"
}

resource "aws_lb_target_group" "this" {
  name        = local.lb_tg_card_query
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
    Name = local.lb_tg_card_query
  }
}
resource "aws_lb_listener_rule" "card_query_listener_rule" {
  listener_arn = var.listener_http_arn
  priority     = 99

  condition {
    path_pattern {
      values = ["/graphql"]
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
    Name = "card-query-listener-http-rule"
  }
}
