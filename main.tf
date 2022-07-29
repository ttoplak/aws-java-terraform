# terraform modules
module "playground" {
  # path for our other terraform files
  source = "./terraform"
}

provider "aws" {
  region  = "us-east-1"
}