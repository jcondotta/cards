terraform {
  cloud {
    organization = "jcondotta"

    workspaces {
      name = "cards-bootstrap-prod"
    }
  }
}