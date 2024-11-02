# Define the API Gateway REST API for the cards service
resource "aws_api_gateway_rest_api" "cards_api" {
  name        = "cards-api-${var.environment}"
  description = "API Gateway for cards service in ${var.environment} environment"

  tags = var.tags
}
#
## Define the /graphql resource under the API root
#resource "aws_api_gateway_resource" "graphql" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_rest_api.cards_api.root_resource_id
#  path_part   = "graphql"
#}
#
#
## Define the /api resource
#resource "aws_api_gateway_resource" "api" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_rest_api.cards_api.root_resource_id
#  path_part   = "api"
#}
#
## Define the /v1 resource under /api
#resource "aws_api_gateway_resource" "v1" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.api.id
#  path_part   = "v1"
#}
#
## Define the /cards resource under /v1
#resource "aws_api_gateway_resource" "cards" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.v1.id
#  path_part   = "cards"
#}
#
## Define the /card-id resource under /cards
#resource "aws_api_gateway_resource" "card_id" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.cards.id
#  path_part   = "card-id"
#}
#
## Define the {card-id} path parameter under /card-id
#resource "aws_api_gateway_resource" "card_id_param" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.card_id.id
#  path_part   = "{card-id}"
#}
#
## Define the /activation resource under {card_id}
#resource "aws_api_gateway_resource" "activation" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.card_id_param.id
#  path_part   = "activation"
#}
#
## Define the /cancellation resource under {card_id}
#resource "aws_api_gateway_resource" "cancellation" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.card_id_param.id
#  path_part   = "cancellation"
#}
#
## Define the /lock resource under {card_id}
#resource "aws_api_gateway_resource" "lock" {
#  rest_api_id = aws_api_gateway_rest_api.cards_api.id
#  parent_id   = aws_api_gateway_resource.card_id_param.id
#  path_part   = "lock"
#}