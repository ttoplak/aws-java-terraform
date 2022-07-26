variable "lambda_payload_filename" {
  default = "target/myapp-1.0-SNAPSHOT.jar"
}

variable "lambda_runtime" {
  default = "java11"
}

variable "api_env_stage_name" {
  default = "terraform-lambda-java-stage"
}