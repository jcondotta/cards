data "aws_caller_identity" "current" {
}

data "terraform_remote_state" "bootstrap" {
  backend = "remote"
  config = {
    organization = "jcondotta"
    workspaces = {
      name = "cards-bootstrap-prod"
    }
  }
}