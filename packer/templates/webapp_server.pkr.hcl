packer {
  required_plugins {
    googlecompute = {
      version = ">= 1.0.0"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}

source "googlecompute" "webapp_builder" {
  project_id          = var.project_id
  source_image_family = var.source_image_family
  machine_type        = var.machine_type
  zone                = var.deployment_zone
  network             = var.vpc_network # If not mentioned, default network will be selected
  image_name          = "webapp-${var.source_image_family}-${formatdate("YYYYMMDDHHmmss", timestamp())}"
  image_family        = var.image_family
  image_description   = "Custom image for web applications"
  ssh_username        = var.ssh_username
  tags                = ["packer-build", "webapp"]
  image_labels = {
    environment = "${var.environment}",
    application = "webapp",
    project     = "${var.project_id}",
    private     = "true",
  }
}

build {
  sources = ["source.googlecompute.webapp_builder"]

  provisioner "shell" {
    inline = [
      "if [[ \"${var.environment}\" != \"dev\" ]]; then",
      "  echo 'Upgrading using dnf'",
      "  sudo dnf upgrade -y",
      "fi"
    ]
  }

  provisioner "shell" {
    scripts = [
      "${path.root}/../scripts/setup-java-maven.sh"
    ]
    execute_command = "chmod +x {{.Path}}; sudo {{.Path}}"
  }

  provisioner "file" {
    source      = "${var.artifact_path}"
    destination = "/tmp/CloudNativeApplication-0.0.1-SNAPSHOT.jar"
    max_retries = 5
  }

  provisioner "shell" {
    scripts = [
      "${path.root}/../scripts/setup-user-privileges.sh",
      "${path.root}/../scripts/setup-disable-selinux.sh"
    ]
  }

  provisioner "file" {
    source      = "${path.root}/../services/webapp.service"
    destination = "/tmp/webapp.service"
  }

  provisioner "shell" {
    script = "${path.root}/../scripts/setup-systemd-service.sh"
  }

  provisioner "file" {
    source      = "${path.root}/../ops-config/ops-agent-config.yaml"
    destination = "/tmp/ops-agent-config.yaml"
  }

  provisioner "shell" {
    script = "${path.root}/../scripts/setup-ops-agent.sh"
  }

}

variable "environment" {
  type = string
}

variable "project_id" {
  type = string
}

variable "deployment_zone" {
  type = string
}

variable "vpc_network" {
  type = string
}

variable "machine_type" {
  type = string
}

variable "source_image_family" {
  type = string
}

variable "image_family" {
  type = string
}

variable "ssh_username" {
  type = string
}

variable "artifact_path" {
  type    = string
  default = "target/CloudNativeApplication-0.0.1-SNAPSHOT.jar"
}
