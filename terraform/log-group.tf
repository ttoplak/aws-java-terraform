// Create a log group for the lambda
resource "aws_cloudwatch_log_group" "log_group_get" {
  name = "/aws/lambda/products_get"
}

resource "aws_cloudwatch_log_group" "log_group_post" {
  name = "/aws/lambda/products_post"
}

resource "aws_cloudwatch_log_group" "log_group_put" {
  name = "/aws/lambda/products_put"
}

# allow lambda to log to cloudwatch
data "aws_iam_policy_document" "cloudwatch_log_group_access_document" {
  statement {
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]

    resources = [
      "arn:aws:logs:::*",
    ]
  }
}