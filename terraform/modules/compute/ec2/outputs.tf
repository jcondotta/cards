output "instance_ids" {
  description = "IDs of the created EC2 instances"
  value       = [for instance in aws_instance.this : instance.id]
}
