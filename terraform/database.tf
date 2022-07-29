resource "aws_dynamodb_table" "basic-dynamodb-table" {
  name           = "Products"
  billing_mode   = "PROVISIONED"
  read_capacity  = 20
  write_capacity = 20
  hash_key       = "ProductId"
  stream_enabled = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  attribute {
    name = "ProductId"
    type = "S"
  }

  tags = {
    Name        = "dynamodb-product-table-1"
    Environment = "production"
  }
}