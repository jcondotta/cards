# Deploy the API Gateway (ensure all methods are deployed)
resource "aws_api_gateway_deployment" "cards_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.cards_api.id
  stage_name  = var.environment

  # Ensure all integrations are deployed
  depends_on = [
    aws_api_gateway_integration.post_add_card_integration,
    aws_api_gateway_integration.patch_activate_card_integration,
    aws_api_gateway_integration.patch_cancel_card_integration,
    aws_api_gateway_integration.patch_lock_card_integration,
  ]
}