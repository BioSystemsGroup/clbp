#! /usr/bin/Rscript
#source("~/R/misc.r")
column  <- "disability"
argv <- commandArgs(TRUE)

capvaltype <- function(s) {
  cap <- function(s) paste(toupper(substring(s, 1, 1)),
  {s <- substring(s, 2); tolower(s)},
    sep = "", collapse = "-")
  sapply(strsplit(s, split = "-"), cap, USE.NAMES = !is.null(names(s)))
}

usage <- function() {
  print("Usage: avgnval.r [odi|eq5d|vas] [robust|frail|pre-frail] <exp CSV files>")
  quit()
}

datset <- toupper(argv[1])
if (is.na(match(datset,c("ODI","EQ5D","VAS")))) usage()

valtype <- capvaltype(argv[2])
if (is.na(match(valtype,c("Robust","Frail","Pre-Frail")))) usage()

valtypemn <- make.names(valtype)
valmean <- paste(datset,valtypemn,"Mean",sep='.')
valciw <- paste(datset,valtypemn,"CIW",sep='.')

exps  <- argv[-(1:2)]
dat  <- vector("list")
ndx  <- 1
dcols <- list()
for (exp in exps) {
  dat[[ndx]]  <- read.csv(paste(exp,"Comp-Context.csv",sep='/'))
  dcols[[ndx]]  <- dat[[ndx]][,column]
  ndx  <- ndx+1
}

val <- read.csv("data/means-961-03Apr20.csv")
valt <- val[,"Time"]+9

png(paste("avgnvalplot-",valtype,".png",sep=''),width=600,height=300)
##svg(paste("avgnvalplot-",datset,"-",valtype,".svg",sep=''),width=8,height=8)
par(mar=c(4,4,2,2))

dcols  <- do.call(cbind,dcols)
dcolmean <- rowMeans(dcols)
dcolmads <- matrixStats::rowMads(dcols)
tcol <- dat[[1]][,"Time"]
averaged <- cbind(tcol,dcolmean,dcolmads)
colnames(averaged) <- c("Time","μ","ν")

ylim.lower  <- Inf
ylim.upper  <- -Inf
for (ndx in 1:length(dat)) {
  ylim.lower  <- min(ylim.lower, averaged[,"μ"], val[,valmean]-val[,valciw]*val[,valmean], na.rm=T)
  ylim.upper  <- max(ylim.upper, averaged[,"μ"], val[,valmean]+val[,valciw]*val[,valmean], na.rm=T)
}

plot(averaged[,"Time"],averaged[,"μ"],
     main=valtype,
     ylab=paste("μ(",column,")",sep=""),
     xlab="Time",
     type="o",
     col=1,
     ylim=c(ylim.lower,ylim.upper))
##lines(averaged[,"Time"],averaged[,"μ"]+averaged[,"ν"],lty=2)
##lines(averaged[,"Time"],averaged[,"μ"]-averaged[,"ν"],lty=2)
lines(valt,val[,valmean],col=2)
points(valt,val[,valmean],col=2)
arrows(valt, val[,valmean]-val[,valciw]*val[,valmean], valt, val[,valmean]+val[,valciw]*val[,valmean], length=0.05, angle=90, code=3)

legend("bottomright",legend=c("experimental",datset),col=1:2,lty=1)
grid()

