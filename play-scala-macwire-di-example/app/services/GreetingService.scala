package services

import models.Entry
import repository.BaseRepository

class GreetingService(repository: BaseRepository) {

  def greetingMessage(language: String) = language match {
    case "it" => "Messi"
    case _ => "Hello"
  }

  def insert(entry: Entry) {
    repository.insert(entry)
  }

}
