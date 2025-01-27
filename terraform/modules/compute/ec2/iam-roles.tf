resource "aws_iam_role" "this" {
  name = "cards-ec2-${var.environment}-access-role"

  assume_role_policy = jsonencode(
    {
      Version = "2012-10-17",
      Statement = [
        {
          Action = "sts:AssumeRole",
          Effect = "Allow",
          Principal = {
            Service = "ec2.amazonaws.com"
          }
        }
      ]
    }
  )
}

resource "aws_iam_policy" "this" {
  name        = "cards-ec2-${var.environment}-access-policy"
  description = "IAM Policy to grant EC2 instances access"

  policy = jsonencode(
    {
      Version = "2012-10-17",
      Statement = [
        {
          "Effect": "Allow",
          "Action": "logs:CreateLogGroup",
          "Resource": "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/ec2/*",
          "Sid": "AllowLogGroupCreationForEC2"
        },
        {
          "Effect": "Allow",
          "Action": [
            "logs:CreateLogStream",
            "logs:PutLogEvents"
          ],
          "Resource": "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/ec2/*",
          "Sid": "AllowLogStreamCreationAndLogEventsForEC2"
        },
        {
          "Effect": "Allow",
          "Action": [
            "dynamodb:GetItem",
            "dynamodb:Query"
          ],
          "Resource": [
            var.dynamodb_cards_table_arn,
            "${var.dynamodb_cards_table_arn}/index/*"
          ],
          "Sid": "AllowDynamoDBReadAccessToCardsTable"
        }
      ]
    }
  )
}

resource "aws_iam_role_policy_attachment" "this" {
  role       = aws_iam_role.this.name
  policy_arn = aws_iam_policy.this.arn
}

resource "aws_iam_instance_profile" "this" {
  name = "cards-ec2-${var.environment}-instance-profile"
  role = aws_iam_role.this.name
}