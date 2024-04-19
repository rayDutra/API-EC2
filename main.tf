provider "aws" {
    region                      = "sa-east-1"
    access_key                  = "teste"
    secret_key                  = "teste"
    skip_credentials_validation = true
    skip_requesting_account_id  = true
    skip_metadata_api_check     = true

    endpoints {
        dynamodb = "http://localhost:4566"
        ec2      = "http://localhost:4566"
    }
}

resource "aws_dynamodb_table" "user_table" {
    name           = "User"
    billing_mode   = "PROVISIONED"
    read_capacity  = 20
    write_capacity = 20
    hash_key       = "id"

    attribute {
        name = "id"
        type = "S"
    }

    attribute {
        name = "email"
        type = "S"
    }

    global_secondary_index {
        name               = "EmailIndex"
        hash_key           = "email"
        write_capacity     = 10
        read_capacity      = 10
        projection_type    = "INCLUDE"
        non_key_attributes = ["id"]
    }

    tags = {
        Name        = "user-table"
        Environment = "production"
    }
}

#resource "aws_instance" "example" {
#    ami           = "ami-12345678"
#    instance_type = "t2.micro"
#    key_name      = "my-keypair"
#
#    provider      = aws
#    tags = {
#        Name = "example-instance"
#    }
#}

resource "aws_instance" "localstack_instance" {
    ami                    = "ami-1234"
    instance_type          = "t2.micro"
    key_name               = "localstack_key"

    tags = {
        Name = "LocalStack_Instance"
    }
}
resource "aws_ecs_task_definition" "localstack_task" {
    family                   = "localstack_task"
    container_definitions   = <<DEFINITION
[
  {
    "name": "meu_container",
    "image": "openjdk:17-jdk-alpine",
    "cpu": 256,
    "memory": 512,
    "essential": true,
    "portMappings": [
      {
        "containerPort": 8080,
        "hostPort": 8080
      }
    ],
    "command": ["java", "-jar", "user/target/project-0.0.1-SNAPSHOT.jar"],
    "environment": []
  }
]
DEFINITION
}
