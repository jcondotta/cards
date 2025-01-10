terraform {
  cloud {
    organization = "jcondotta"

    workspaces {
      name = "cards-prod"
    }
  }
}