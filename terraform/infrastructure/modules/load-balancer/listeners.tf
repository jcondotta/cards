resource "aws_lb_listener" "load_balancer_listener_http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/html"
      message_body = "<h1>404 Not Found</h1><p>Cards - Custom error page</p>"
      status_code  = "404"
    }
  }

  tags = {
    Name = "load-balancer-listener-http"
  }
}