resource "aws_instance" "this" {
  for_each = tomap(
    {
      for idx, subnet_id in var.public_subnet_ids :
      "instance_${idx}" => subnet_id
    }
  )

  ami           = "ami-03ecf97a3bb0705c2" # Amazon Linux 2023 AMI 2023.6.20250107.0 arm64 HVM kernel-6.1
  instance_type = "t4g.nano"
  key_name      = "jcondotta-ssh-key"
  monitoring    = true

  subnet_id = each.value

  vpc_security_group_ids = [
    aws_security_group.ec2_security_group.id
  ]

  # User data script for Java 21 installation
  user_data = <<-EOT
    #!/bin/bash
    yum update -y
    rpm --import https://yum.corretto.aws/corretto.key
    curl -L https://yum.corretto.aws/corretto.repo | tee /etc/yum.repos.d/corretto.repo
    yum install -y java-21-amazon-corretto-devel
  EOT

  tags = {
    Name = "cards-ec2-${each.key}"
  }
}