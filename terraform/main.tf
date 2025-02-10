module "networking" {
  source = "./modules/networking"

  aws_region  = var.aws_region
  environment = var.environment
}

module "dynamodb" {
  source = "./modules/data/dynamodb"

  cards_table_name                  = "cards-${var.environment}"
  cards_by_bank_account_id_gsi_name = "cards-by-bank-account-id-gsi-${var.environment}"
}

module "sns" {
  source = "./modules/messaging/sns"

  sqs_card_application_dlq_name = module.sqs.card_application_dead_letter_queue_name
}

module "sqs" {
  source = "./modules/messaging/sqs"

  card_application_queue_name = "card-application-${var.environment}"
  card_application_dlq_name   = "card-application-dlq-${var.environment}"

  sns_topic_card_application_dlq_notification_arn = module.sns.card_application_dlq_notifications_arn
}

module "s3" {
  source = "./modules/storage/s3"

  cards_process_service_lambda_files_bucket_name = "cards-process-service-lambda-files-${var.environment}"
}

module "caching" {
  source = "./modules/caching"

  environment = var.environment

  vpc_id             = module.networking.vpc_id
  cache_cluster_name = "cards-cache-${var.environment}"
  subnet_ids         = module.networking.private_subnet_ids
  security_group_ids = [
    module.cards_process_service_lambda.process_cards_lambda_security_group_id
  ]
}

module "cards_process_service_lambda" {
  source = "./modules/compute/lambda/process-service"

  aws_region             = var.aws_region
  environment            = var.environment
  current_aws_account_id = data.aws_caller_identity.current.account_id

  function_name = "cards-process-service-${var.environment}"

  dynamodb_cards_table_arn                       = module.dynamodb.cards_table_arn
  sqs_card_application_queue_arn                 = module.sqs.card_application_queue_arn
  cards_process_service_lambda_files_bucket_name = module.s3.cards_process_service_lambda_files_bucket_name

  vpc_id     = module.networking.vpc_id
  subnet_ids = module.networking.private_subnet_ids

  environment_variables = {
    "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
    "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
    "REDIS_HOST" = module.caching.redis_host,
    "REDIS_PORT" = module.caching.redis_port,
  }
}

#
#
# # module "load_balancer" {
# #   source = "./modules/load-balancer"
# #
# #   environment        = var.environment
# #   load_balancer_name = "cards-load-balancer-${var.environment}"
# #
# #   vpc_id     = module.networking.vpc_id
# #   subnet_ids = module.networking.public_subnet_ids
# # }
# #
# # module "card_management_lb_target_group" {
# #   source = "./modules/load-balancer/target-groups"
# #
# #   vpc_id = module.networking.vpc_id
# #
# #   target_group_name     = "card-management-lb-tg-${var.environment}"
# #   target_group_port     = 8072
# #   target_group_protocol = "HTTP"
# # }
# #
# # module "card_query_lb_target_group" {
# #   source = "./modules/load-balancer/target-groups"
# #
# #   vpc_id = module.networking.vpc_id
# #
# #   target_group_name     = "card-query-lb-tg-${var.environment}"
# #   target_group_port     = 8073
# #   target_group_protocol = "HTTP"
# # }
# #
# # module "card_management_lb_listener_rule" {
# #   source = "./modules/load-balancer/listener-rules"
# #
# #   load_balancer_listener_arn = module.load_balancer.listener_http_arn
# #   listener_rule_name         = "card-management-listener-rule-${var.environment}"
# #   listener_rule_priority     = 100
# #
# #   path_pattern_values = [
# #     "/manage/*"
# #   ]
# #   target_group_arn = module.card_management_lb_target_group.target_group_arn
# # }
# #
# # module "card_query_lb_listener_rule" {
# #   source = "./modules/load-balancer/listener-rules"
# #
# #   load_balancer_listener_arn = module.load_balancer.listener_http_arn
# #   listener_rule_name         = "card-query-listener-rule-${var.environment}"
# #   listener_rule_priority     = 100
# #
# #   path_pattern_values = [
# #     "/query/*"
# #   ]
# #   target_group_arn = module.card_query_lb_target_group.target_group_arn
# # }
# #
# # module "ecs_cluster" {
# #   source = "./modules/compute/ecs/cluster"
# #
# #   ecs_cluster_name = "cards-ecs-cluster-${var.environment}"
# # }
# #
# # module "ecs_card_management_service_task_definition" {
# #   source = "./modules/compute/ecs/task-definitions/management-service"
# #
# #   aws_region             = var.aws_region
# #   environment            = var.environment
# #   current_aws_account_id = data.aws_caller_identity.current.account_id
# #
# #   dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
# #   sqs_card_application_queue_arn = module.sqs.card_application_queue_arn
# #
# #   container_port = module.card_management_lb_target_group.target_group_port
# #
# #   environment_variables = {
# #     "AWS_DEFAULT_REGION"                  = var.aws_region
# #     "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
# #     "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
# #   }
# # }
# #
# # module "ecs_card_management_service_security_group" {
# #   source = "./modules/compute/ecs/security-groups"
# #
# #   name_prefix    = "ecs-card-management-service-sg-${var.environment}"
# #   vpc_id         = module.networking.vpc_id
# #   container_port = module.ecs_card_management_service_task_definition.container_port
# #
# #   ingress_security_group_ids = [
# #     module.load_balancer.security_group_id
# #   ]
# # }
# #
# # module "ecs_card_management_service" {
# #   source = "./modules/compute/ecs/services"
# #
# #   ecs_cluster_id   = module.ecs_cluster.ecs_cluster_id
# #   ecs_service_name = "card-management-service-${var.environment}"
# #
# #   desired_count = 1
# #   service_security_group_ids = [
# #     module.ecs_card_management_service_security_group.ecs_service_task_security_group_id
# #   ]
# #   service_subnet_ids = module.networking.private_subnet_ids
# #
# #   task_definition_arn = module.ecs_card_management_service_task_definition.ecs_task_definition_arn
# #
# #   container_name = module.ecs_card_management_service_task_definition.container_name
# #   container_port = module.ecs_card_management_service_task_definition.container_port
# #
# #   load_balancer_target_group_arn = module.card_management_lb_target_group.target_group_arn
# # }
# #
# # # module "ecs_cards_management_service" {
# # #   source = "./modules/compute/ecs/cards-management-service"
# # #
# # #   aws_region             = var.aws_region
# # #   environment            = var.environment
# # #   current_aws_account_id = data.aws_caller_identity.current.account_id
# # #
# # #   vpc_id                          = module.networking.vpc_id
# # #   service_subnet_ids              = module.networking.private_subnet_ids
# # #   load_balancer_target_group_arn  = module.load_balancer.ecs_card_management_tg_arn
# # #   load_balancer_security_group_id = module.load_balancer.security_group_id
# # #
# # #   dynamodb_cards_table_arn       = module.dynamodb.cards_table_arn
# # #   sqs_card_application_queue_arn = module.sqs.card_application_queue_arn
# # #
# # #   environment_variables = {
# # #     "AWS_DEFAULT_REGION"                  = var.aws_region
# # #     "AWS_DYNAMODB_CARDS_TABLE_NAME"       = module.dynamodb.cards_table_name,
# # #     "AWS_SQS_CARD_APPLICATION_QUEUE_NAME" = module.sqs.card_application_queue_name,
# # #   }
# # # }
# # #