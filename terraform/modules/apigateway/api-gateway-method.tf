# Define the POST method for /api/v1/cards
resource "aws_api_gateway_method" "post_add_card" {
  rest_api_id   = aws_api_gateway_rest_api.cards_api.id
  resource_id   = aws_api_gateway_resource.cards.id
  http_method   = "POST"
  authorization = "NONE"
}

# Integration for the POST method for /api/v1/cards
resource "aws_api_gateway_integration" "post_add_card_integration" {
  rest_api_id = aws_api_gateway_rest_api.cards_api.id
  resource_id = aws_api_gateway_resource.cards.id
  http_method = aws_api_gateway_method.post_add_card.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.management_cards_lambda_invoke_uri
}

# Define the PATCH method for /api/v1/cards/card-id/{card-id}/activation
resource "aws_api_gateway_method" "patch_activate_card" {
  rest_api_id   = aws_api_gateway_rest_api.cards_api.id
  resource_id   = aws_api_gateway_resource.activation.id
  http_method   = "PATCH"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.card-id" = true
  }
}

# Integration for the PATCH method for /api/v1/cards/card-id/{card-id}/activation
resource "aws_api_gateway_integration" "patch_activate_card_integration" {
  rest_api_id = aws_api_gateway_rest_api.cards_api.id
  resource_id = aws_api_gateway_resource.activation.id
  http_method = aws_api_gateway_method.patch_activate_card.http_method

  integration_http_method = "POST" # Should be POST for a Lambda integration
  type                    = "AWS_PROXY"
  uri                     = var.management_cards_lambda_invoke_uri
}

# Define the PATCH method for /api/v1/cards/card-id/{card-id}/cancellation
resource "aws_api_gateway_method" "patch_cancel_card" {
  rest_api_id   = aws_api_gateway_rest_api.cards_api.id
  resource_id   = aws_api_gateway_resource.cancellation.id
  http_method   = "PATCH"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.card-id" = true
  }
}

# Integration for the PATCH method for /api/v1/cards/card-id/{card-id}/cancellation
resource "aws_api_gateway_integration" "patch_cancel_card_integration" {
  rest_api_id = aws_api_gateway_rest_api.cards_api.id
  resource_id = aws_api_gateway_resource.cancellation.id
  http_method = aws_api_gateway_method.patch_cancel_card.http_method

  integration_http_method = "POST" # Should be POST for a Lambda integration
  type                    = "AWS_PROXY"
  uri                     = var.management_cards_lambda_invoke_uri
}

# Define the PATCH method for /api/v1/cards/card-id/{card-id}/lock
resource "aws_api_gateway_method" "patch_lock_card" {
  rest_api_id   = aws_api_gateway_rest_api.cards_api.id
  resource_id   = aws_api_gateway_resource.lock.id
  http_method   = "PATCH"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.card-id" = true
  }
}

# Integration for the PATCH method for /api/v1/cards/card-id/{card-id}/lock
resource "aws_api_gateway_integration" "patch_lock_card_integration" {
  rest_api_id = aws_api_gateway_rest_api.cards_api.id
  resource_id = aws_api_gateway_resource.lock.id
  http_method = aws_api_gateway_method.patch_lock_card.http_method

  integration_http_method = "POST" # Should be POST for a Lambda integration
  type                    = "AWS_PROXY"
  uri                     = var.management_cards_lambda_invoke_uri
}
