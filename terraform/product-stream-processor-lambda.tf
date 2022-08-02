# defining our lambda and referencing to our java handler function
resource "aws_lambda_function" "product_stream_processor" {
  runtime          = var.lambda_runtime
  filename      = var.lambda_payload_filename
  source_code_hash = base64sha256(filebase64(var.lambda_payload_filename))
  function_name = "product_stream_processor"
  # lambda handler function name, it will be full class path name with package name
  handler          = "com.example.myapp.products.handlers.ProductStreamProcessor" #package-name.class-name
  timeout = 60
  memory_size = 256
  # role for lambda is defined in aws-iam_role resource
  role             = aws_iam_role.iam_role_for_lambda.arn
  depends_on   = ["aws_cloudwatch_log_group.product_stream_processor", "aws_dynamodb_table.basic-dynamodb-table"]
}

resource "aws_lambda_event_source_mapping" "product-stream-processor-mapping" {
  event_source_arn  = aws_dynamodb_table.basic-dynamodb-table.stream_arn
  function_name     = aws_lambda_function.product_stream_processor.arn
  batch_size        = 1
  starting_position = "LATEST"
}

resource "aws_sqs_queue" "product_stream_processor_results_queue" {
  name                      = "product-stream-processor-results-queue"
  delay_seconds             = 90
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.terraform_queue_deadletter.arn
    maxReceiveCount     = 4
  })
}

resource "aws_sqs_queue" "terraform_queue_deadletter" {
  name = "terraform-example-deadletter-queue"
  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue",
    sourceQueueArns   = [aws_sqs_queue.product_stream_processor_results_queue.arn]
  })
}