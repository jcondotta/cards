output "ec2_security_group_id" {
  description = "The IDs of the public subnets"
  value       = aws_security_group.ec2_security_group.id
}

output "instance_ids" {
  description = "IDs of the created EC2 instances"
  value       = [for instance in aws_instance.this : instance.id]
}

output "instance_public_ips" {
  description = "Public IPs of the created EC2 instances"
  value       = [for instance in aws_instance.this : instance.public_ip]
}
