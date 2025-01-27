resource "aws_instance" "this" {
  for_each = tomap(
    {
      for idx, subnet_id in var.subnet_ids :
      "instance-${var.environment}-${idx + 1}" => subnet_id
    }
  )

  ami           = "ami-03ecf97a3bb0705c2" # Amazon Linux 2023 AMI 2023.6.20250107.0 arm64 HVM kernel-6.1
  instance_type = "t4g.nano"
  key_name      = "jcondotta-ssh-key"
  monitoring    = true

  subnet_id = each.value

  iam_instance_profile = aws_iam_instance_profile.this.name
  vpc_security_group_ids = [
    aws_security_group.ec2_cards_security_group.id
  ]

  # User data script for Java 21 installation
  user_data = <<-EOT
    #!/bin/bash
    yum update -y
    rpm --import https://yum.corretto.aws/corretto.key
    curl -L https://yum.corretto.aws/corretto.repo | tee /etc/yum.repos.d/corretto.repo
    yum install -y java-21-amazon-corretto-devel

    echo "export AWS_DEFAULT_REGION=${var.aws_region}" >> /etc/environment
    echo "export AWS_DYNAMODB_CARDS_TABLE_NAME=cards-${var.environment}" >> /etc/environment
    echo "export AWS_DYNAMODB_CARDS_TABLE_NAME=cards-by-bank-account-id-gsi-${var.environment}" >> /etc/environment
    source /etc/environment
  EOT

  tags = {
    Name = "cards-ec2-${each.key}"
  }
}