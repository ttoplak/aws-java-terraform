resource "aws_api_gateway_rest_api" "rest_api" {
  name        = "rest_api"
  description = "Java Lambda on Terrraform"
}

resource "aws_api_gateway_resource" "products" {
  rest_api_id = aws_api_gateway_rest_api.rest_api.id
  parent_id   = aws_api_gateway_rest_api.rest_api.root_resource_id
  path_part   = "products"
}

resource "aws_api_gateway_resource" "product_by_id" {
  rest_api_id = aws_api_gateway_rest_api.rest_api.id
  parent_id   = aws_api_gateway_resource.products.id
  path_part   = "{productId}"
}

resource "aws_api_gateway_method" "products_get" {
  rest_api_id   = aws_api_gateway_rest_api.rest_api.id
  resource_id   = aws_api_gateway_resource.products.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "products_get_integration" {
  rest_api_id             = aws_api_gateway_rest_api.rest_api.id
  resource_id             = aws_api_gateway_resource.products.id
  http_method             = aws_api_gateway_method.products_get.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.products_get.invoke_arn
}

resource "aws_api_gateway_method" "products_post" {
  rest_api_id   = aws_api_gateway_rest_api.rest_api.id
  resource_id   = aws_api_gateway_resource.products.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "products_post_integration" {
  rest_api_id             = aws_api_gateway_rest_api.rest_api.id
  resource_id             = aws_api_gateway_resource.products.id
  http_method             = aws_api_gateway_method.products_post.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.products_post.invoke_arn
}

resource "aws_api_gateway_method" "products_put" {
  rest_api_id   = aws_api_gateway_rest_api.rest_api.id
  resource_id   = aws_api_gateway_resource.product_by_id.id
  http_method   = "PUT"
  authorization = "NONE"

  request_parameters = {
    "method.request.path.productId" = true
  }
}

resource "aws_api_gateway_integration" "products_put_integration" {
  rest_api_id             = aws_api_gateway_rest_api.rest_api.id
  resource_id             = aws_api_gateway_resource.product_by_id.id
  http_method             = aws_api_gateway_method.products_put.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.products_put.invoke_arn

  request_parameters = {
    "integration.request.path.productId" = "method.request.path.productId"
  }
}


resource "aws_api_gateway_deployment" "rest_api_deployment" {
  depends_on  = [
    "aws_api_gateway_integration.products_get_integration",
    "aws_api_gateway_integration.products_post_integration",
    "aws_api_gateway_integration.products_put_integration"
  ]
  rest_api_id = aws_api_gateway_rest_api.rest_api.id
  stage_name  = var.api_env_stage_name
}