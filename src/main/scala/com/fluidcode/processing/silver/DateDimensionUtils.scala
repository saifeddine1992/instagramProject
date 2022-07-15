package com.fluidcode.processing.silver

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateDimensionUtils {

 def generateDates(startDate: LocalDate, endDate: LocalDate): List[LocalDate] = {
   def streamDates(start: LocalDate): Stream[LocalDate] = {
     start #:: streamDates(start plusDays 1)
   }
   streamDates(startDate).takeWhile(_.isBefore(endDate.plusDays(1))).toList
 }

 def getDescription(date: LocalDate) : String = {

   date.getDayOfWeek.toString ++ ", "+ date.getMonth.toString +" " + date.getDayOfMonth.toString + ", " +date.getYear.toString
 }

 def getQuarter(date: LocalDate) : String = {
   val resultOfEquation = (date.getMonthValue - 1)/3 + 1
   "Q" + resultOfEquation.toString
   }
 def isWeekend (date: LocalDate) : Boolean = {
   if (date.getDayOfWeek.toString == "SUNDAY" || date.getDayOfWeek.toString == "SATURDAY"){
     true
   }
   else {
     false
   }
 }


def isEasterMonday (date : LocalDate): LocalDate = {
  val year = date.getYear
  val a = year % 19
  val b = year / 100
  val c = year % 100
  val d = b / 4
  val e = b % 4
  val g = (8 * b + 13) / 25
  val h = (19 * a + b - d - g + 15) % 30
  val j = c / 4
  val k = c % 4
  val m = (a + 11 * h) / 319
  val r = (2 * e + 2 * j - k - h + m + 32) % 7
  val nMonth = (h - m + r + 90) / 25
  val eastDay = (h - m + r + nMonth + 19) % 32

  fixDateFormat(nMonth,eastDay,year).plusDays(1)
}
  def matchingEasterMonday(date: LocalDate): String = {
    isEasterMonday(date).getDayOfMonth + "-" + isEasterMonday(date).getMonthValue.toString
  }

def isAscensionDay(date : LocalDate): String = {
  isEasterMonday(date).plusDays(38).getDayOfMonth + "-" + isEasterMonday(date).plusDays(38).getMonthValue.toString
}

def isWhitMonday (date : LocalDate): String = {
  isEasterMonday(date).plusDays(49).getDayOfMonth + "-" + isEasterMonday(date).plusDays(49).getMonthValue.toString
}


 def isHoliday(date: LocalDate) : Boolean = {

    (date.getDayOfWeek.toString , date.getDayOfMonth+ "-" + date.getMonthValue.toString) match {
      case (anyday , "1-1") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "14-2") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "1-5") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "8-5") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "14-7") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "15-8") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "1-11") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "11-11") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "25-12") if anyday == date.getDayOfWeek.toString => true
      case (anyday, "26-12") if anyday == date.getDayOfWeek.toString => true
      case (anyday, easter) if anyday == date.getDayOfWeek.toString && easter == matchingEasterMonday(date) => true
      case (anyday, ascension) if anyday == date.getDayOfWeek.toString && ascension == isAscensionDay(date) => true
      case (anyday, whit) if anyday == date.getDayOfWeek.toString && whit == isWhitMonday(date) => true
      case ("SATURDAY", anyDate) if anyDate == date.getDayOfMonth+ "-" + date.getMonthValue.toString => true
      case ("SUNDAY", anyDate) if anyDate == date.getDayOfMonth+ "-" + date.getMonthValue.toString => true
      case _ => false
    }
  }

  def fixDateFormat(month: Int, day: Int, year: Int): LocalDate = {
    val dateFormat = "yyyy-MM-dd"
    val formatter = DateTimeFormatter.ofPattern(dateFormat)

    if (month < 10 & day < 10) {
      LocalDate.parse(year.toString+"-"+"0"+month.toString+"-"+"0"+day.toString, formatter)
    }
    else if (month > 9 & day < 10) {
      LocalDate.parse(year.toString+"-"+month.toString+"-"+"0"+day.toString, formatter)
    }
    else if (month < 10 & day > 9){
      LocalDate.parse(year.toString+"-"+"0"+month.toString+"-"+day.toString, formatter)
    }
    else {
      LocalDate.parse(year.toString+"-"+month.toString+"-"+day.toString, formatter)
    }
  }

}