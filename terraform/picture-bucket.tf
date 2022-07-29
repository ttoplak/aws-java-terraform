resource "aws_s3_bucket" "image_bucket" {
  bucket = "toplak-playground-bucket"
}

resource "aws_s3_bucket_acl" "image_bucket_acl" {
  bucket = aws_s3_bucket.image_bucket.id
  acl = "public-read"
}