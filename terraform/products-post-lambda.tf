# defining our lambda and referencing to our java handler function
resource "aws_lambda_function" "products_post" {
  runtime          = var.lambda_runtime
  filename      = var.lambda_payload_filename
  source_code_hash = base64sha256(filebase64(var.lambda_payload_filename))
  function_name = "products_post"
  # lambda handler function name, it will be full class path name with package name
  handler          = "com.example.myapp.products.handlers.PostHandler" #package-name.class-name
  timeout = 60
  memory_size = 256
  # role for lambda is defined in aws-iam_role resource
  role             = aws_iam_role.iam_role_for_lambda.arn
  depends_on   = ["aws_cloudwatch_log_group.log_group_post"]
}

# defining permission for our lambda, we have allowed API gateway to
# to invoke our lambda handler function
resource "aws_lambda_permission" "products_post" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.products_post.function_name
  principal     = "apigateway.amazonaws.com"
  # The /*/* portion grants access from any method on any resource
  # within the API Gateway "REST API".
  source_arn = "${aws_api_gateway_deployment.rest_api_deployment.execution_arn}/*/*"
}