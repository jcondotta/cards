resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.cards_load_balancer_target_group.arn
  }

  tags = {
    Name = "cards-http-listener"
  }
}