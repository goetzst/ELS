-- phpMyAdmin SQL Dump
-- version 4.4.3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 15, 2015 at 10:14 PM
-- Server version: 5.6.24
-- PHP Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `els`
--
CREATE DATABASE IF NOT EXISTS `els` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `els`;

-- --------------------------------------------------------

--
-- Table structure for table `bookmarks`
--

DROP TABLE IF EXISTS `bookmarks`;
CREATE TABLE IF NOT EXISTS `bookmarks` (
  `email` varchar(200) NOT NULL,
  `docID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bookmarks`
--

INSERT INTO `bookmarks` (`email`, `docID`) VALUES
('admin.admin@admin.admin', 2);

-- --------------------------------------------------------

--
-- Table structure for table `chapter`
--

DROP TABLE IF EXISTS `chapter`;
CREATE TABLE IF NOT EXISTS `chapter` (
  `chapterID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chapter`
--

INSERT INTO `chapter` (`chapterID`, `docID`, `title`, `sequence`) VALUES
(6, 2, '1 Intro', 1),
(7, 2, '2 Outro', 2),
(8, 3, 'ReferencedChapter', 1);

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `commentID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `contentID` int(11) NOT NULL,
  `content` text NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`commentID`, `email`, `contentID`, `content`) VALUES
(2, 'admin.admin@admin.admin', 25, 'Comment'),
(3, 'admin.admin@admin.admin', 25, 'anotherComment');

-- --------------------------------------------------------

--
-- Table structure for table `content`
--

DROP TABLE IF EXISTS `content`;
CREATE TABLE IF NOT EXISTS `content` (
  `contentID` int(11) NOT NULL,
  `chapterID` int(11) DEFAULT NULL,
  `cheadlineID` int(11) DEFAULT NULL,
  `csubheadlineID` int(11) DEFAULT NULL,
  `content` text,
  `type` int(11) NOT NULL,
  `sequence` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `content`
--

INSERT INTO `content` (`contentID`, `chapterID`, `cheadlineID`, `csubheadlineID`, `content`, `type`, `sequence`) VALUES
(24, 6, NULL, NULL, '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. <span class="technicalTerm">Excepteur</span> sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est <span class="important">laborum</span>.</p>\r\n<p><img title="\\sum_{a=0}^{10}a" src="http://latex.codecogs.com/gif.latex?\\sum_{a=0}^{10}a" alt="" /></p>\r\n<p>&nbsp;</p>', 0, 1),
(25, NULL, NULL, 2, '<img src="/public/uploads/91cf92015-07-15T20_00_38_816.jpg" class="img-responsive insertAutomatically" alt="Responsive image">', 1, 1),
(26, NULL, NULL, 3, '<div style="height: 44px" class="editor" id="editoriskmck">map:: (a -&gt; b) -&gt; [a] -&gt; [b]\r\n\r\nfunc:: Int -&gt; Int -&gt; String</div><script type="text/javascript">var editor = ace.edit("editoriskmck"); editor.setTheme("ace/theme/chrome"); editor.getSession().setMode("ace/mode/haskell"); editor.resize(); editor.setReadOnly(true); editor.session.setNewLineMode("windows");</script>', 2, 1),
(27, NULL, 7, NULL, '<p>But I must explain to you how all this mistaken idea of denouncing of a pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who <strong>loves or pursues or desires to obtain pain of itself, because it is pain, but</strong> occasionally <strong>circumstances occur in which toil and pain can procure him some great</strong> pleasure.</p>', 0, 1),
(28, 8, NULL, NULL, '<p>Referenced Content</p>', 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `contentheadline`
--

DROP TABLE IF EXISTS `contentheadline`;
CREATE TABLE IF NOT EXISTS `contentheadline` (
  `cheadlineID` int(11) NOT NULL,
  `chapterID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contentheadline`
--

INSERT INTO `contentheadline` (`cheadlineID`, `chapterID`, `title`, `sequence`) VALUES
(6, 6, '1.1 Stuff', 1),
(7, 7, '2.1 More Stuff', 1);

-- --------------------------------------------------------

--
-- Table structure for table `contentsubheadline`
--

DROP TABLE IF EXISTS `contentsubheadline`;
CREATE TABLE IF NOT EXISTS `contentsubheadline` (
  `csubheadlineID` int(11) NOT NULL,
  `cheadlineID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contentsubheadline`
--

INSERT INTO `contentsubheadline` (`csubheadlineID`, `cheadlineID`, `title`, `sequence`) VALUES
(2, 6, 'Pictures', 1),
(3, 7, 'Code', 1);

-- --------------------------------------------------------

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `docID` int(11) NOT NULL,
  `subjectID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `documentName` varchar(200) NOT NULL,
  `visible` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `documents`
--

INSERT INTO `documents` (`docID`, `subjectID`, `email`, `documentName`, `visible`) VALUES
(2, 2, 'admin.admin@admin.admin', 'SoPra 2015', 1),
(3, 1, 'admin.admin@admin.admin', 'Reference Doc', 1);

-- --------------------------------------------------------

--
-- Table structure for table `faculties`
--

DROP TABLE IF EXISTS `faculties`;
CREATE TABLE IF NOT EXISTS `faculties` (
  `facultyName` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `faculties`
--

INSERT INTO `faculties` (`facultyName`) VALUES
('PM'),
('TestFaculty');

-- --------------------------------------------------------

--
-- Table structure for table `flashcard`
--

DROP TABLE IF EXISTS `flashcard`;
CREATE TABLE IF NOT EXISTS `flashcard` (
  `flashCardID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `tagID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `flashCardName` varchar(200) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6418900 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcard`
--

INSERT INTO `flashcard` (`flashCardID`, `docID`, `tagID`, `email`, `flashCardName`) VALUES
(6418897, 1, 77889358, 'testmail@mail.com', 'FlashCard'),
(6418899, 2, 77889360, 'admin.admin@admin.admin', 'FlashCard for OtherTag');

-- --------------------------------------------------------

--
-- Table structure for table `flashcardchapter`
--

DROP TABLE IF EXISTS `flashcardchapter`;
CREATE TABLE IF NOT EXISTS `flashcardchapter` (
  `flashCardChapterID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `chapterID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9984769 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardchapter`
--

INSERT INTO `flashcardchapter` (`flashCardChapterID`, `flashCardID`, `title`, `sequence`, `chapterID`) VALUES
(9984765, 6418897, '1 Intro', 1, 1),
(9984766, 6418897, '2 Outro', 2, 3),
(9984767, 6418899, '2 Outro', 2, 7),
(9984768, 6418899, '1 Intro', 1, 6);

-- --------------------------------------------------------

--
-- Table structure for table `flashcardcomment`
--

DROP TABLE IF EXISTS `flashcardcomment`;
CREATE TABLE IF NOT EXISTS `flashcardcomment` (
  `flashCardCommentID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `flashCardContentID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `commentID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardcomment`
--

INSERT INTO `flashcardcomment` (`flashCardCommentID`, `flashCardID`, `flashCardContentID`, `email`, `content`, `commentID`) VALUES
(4, 6418897, 31, 'testmail@mail.com', 'comment', 1);

-- --------------------------------------------------------

--
-- Table structure for table `flashcardcontent`
--

DROP TABLE IF EXISTS `flashcardcontent`;
CREATE TABLE IF NOT EXISTS `flashcardcontent` (
  `flashCardContentID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `flashCardChapterID` int(11) DEFAULT NULL,
  `flashCardHeadlineID` int(11) DEFAULT NULL,
  `flashCardSubHeadlineID` int(11) DEFAULT NULL,
  `content` text NOT NULL,
  `type` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  `contentID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardcontent`
--

INSERT INTO `flashcardcontent` (`flashCardContentID`, `flashCardID`, `flashCardChapterID`, `flashCardHeadlineID`, `flashCardSubHeadlineID`, `content`, `type`, `sequence`, `contentID`) VALUES
(30, 6418897, 9984766, NULL, NULL, '<p>Goodbye</p>', 0, 1, 5),
(31, 6418897, 9984765, NULL, NULL, '<p>text</p>', 0, 1, 1),
(32, 6418897, NULL, 7, NULL, '<p>TextToHeadline</p>', 0, 1, 2),
(33, 6418897, NULL, NULL, 6, '<p>TextToSubHeadline</p>', 0, 1, 3),
(34, 6418899, NULL, 9, NULL, '<p>But I must explain to you how all this mistaken idea of denouncing of a pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who <strong>loves or pursues or desires to obtain pain of itself, because it is pain, but</strong> occasionally <strong>circumstances occur in which toil and pain can procure him some great</strong> pleasure.</p>', 0, 1, 27),
(35, 6418899, NULL, NULL, 7, '<img src="/public/uploads/91cf92015-07-15T20_00_38_816.jpg" class="img-responsive insertAutomatically" alt="Responsive image">', 1, 1, 25);

-- --------------------------------------------------------

--
-- Table structure for table `flashcardheadline`
--

DROP TABLE IF EXISTS `flashcardheadline`;
CREATE TABLE IF NOT EXISTS `flashcardheadline` (
  `flashCardHeadlineID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `flashCardChapterID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `headlineID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardheadline`
--

INSERT INTO `flashcardheadline` (`flashCardHeadlineID`, `flashCardID`, `flashCardChapterID`, `title`, `sequence`, `headlineID`) VALUES
(7, 6418897, 9984765, '1.1 Headline', 1, 1),
(8, 6418899, 9984768, '1.1 Stuff', 1, 6),
(9, 6418899, 9984767, '2.1 More Stuff', 1, 7);

-- --------------------------------------------------------

--
-- Table structure for table `flashcardsubcomment`
--

DROP TABLE IF EXISTS `flashcardsubcomment`;
CREATE TABLE IF NOT EXISTS `flashcardsubcomment` (
  `flashCardSubCommentID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `flashCardCommentID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `subCommentID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardsubcomment`
--

INSERT INTO `flashcardsubcomment` (`flashCardSubCommentID`, `flashCardID`, `flashCardCommentID`, `email`, `content`, `subCommentID`) VALUES
(4, 6418897, 4, 'testmail@mail.com', 'subcomment', 1);

-- --------------------------------------------------------

--
-- Table structure for table `flashcardsubheadline`
--

DROP TABLE IF EXISTS `flashcardsubheadline`;
CREATE TABLE IF NOT EXISTS `flashcardsubheadline` (
  `flashCardSubHeadlineID` int(11) NOT NULL,
  `flashCardID` int(11) NOT NULL,
  `flashCardHeadlineID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `subHeadlineID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flashcardsubheadline`
--

INSERT INTO `flashcardsubheadline` (`flashCardSubHeadlineID`, `flashCardID`, `flashCardHeadlineID`, `title`, `sequence`, `subHeadlineID`) VALUES
(6, 6418897, 7, '1.1.1 SubHeadline', 1, 1),
(7, 6418899, 8, 'Pictures', 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `moderators`
--

DROP TABLE IF EXISTS `moderators`;
CREATE TABLE IF NOT EXISTS `moderators` (
  `email` varchar(200) NOT NULL,
  `docID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `reference`
--

DROP TABLE IF EXISTS `reference`;
CREATE TABLE IF NOT EXISTS `reference` (
  `referenceID` int(11) NOT NULL,
  `contentID` int(11) NOT NULL,
  `referencesToID` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `referencesToDocID` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `reference`
--

INSERT INTO `reference` (`referenceID`, `contentID`, `referencesToID`, `name`, `referencesToDocID`) VALUES
(2, 25, 28, 'CrossDocReference', 3),
(3, 24, 27, 'intraDocReference', 2);

-- --------------------------------------------------------

--
-- Table structure for table `subcomments`
--

DROP TABLE IF EXISTS `subcomments`;
CREATE TABLE IF NOT EXISTS `subcomments` (
  `subCID` int(11) NOT NULL,
  `email` varchar(200) NOT NULL,
  `commentID` int(11) NOT NULL,
  `content` text NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subcomments`
--

INSERT INTO `subcomments` (`subCID`, `email`, `commentID`, `content`) VALUES
(2, 'admin.admin@admin.admin', 2, 'SubComment'),
(3, 'admin.admin@admin.admin', 2, 'another SubComment');

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
CREATE TABLE IF NOT EXISTS `subjects` (
  `subjectID` int(11) NOT NULL,
  `facultyName` varchar(200) NOT NULL,
  `subjectName` varchar(200) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`subjectID`, `facultyName`, `subjectName`) VALUES
(1, 'TestFaculty', 'testSubject'),
(2, 'PM', 'SoPra');

-- --------------------------------------------------------

--
-- Table structure for table `suggestions`
--

DROP TABLE IF EXISTS `suggestions`;
CREATE TABLE IF NOT EXISTS `suggestions` (
  `suggestionID` int(11) NOT NULL,
  `parentID` int(11) NOT NULL,
  `content` text NOT NULL,
  `type` int(11) NOT NULL,
  `parentKind` int(11) NOT NULL,
  `email` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tagchaptersan`
--

DROP TABLE IF EXISTS `tagchaptersan`;
CREATE TABLE IF NOT EXISTS `tagchaptersan` (
  `tagID` int(11) NOT NULL,
  `chapterID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tagchaptersan`
--

INSERT INTO `tagchaptersan` (`tagID`, `chapterID`) VALUES
(77889359, 6),
(77889360, 7);

-- --------------------------------------------------------

--
-- Table structure for table `tagcomments`
--

DROP TABLE IF EXISTS `tagcomments`;
CREATE TABLE IF NOT EXISTS `tagcomments` (
  `tagID` int(11) NOT NULL,
  `commentID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tagcontentheadline`
--

DROP TABLE IF EXISTS `tagcontentheadline`;
CREATE TABLE IF NOT EXISTS `tagcontentheadline` (
  `cheadlineID` int(11) NOT NULL,
  `tagID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tagcontentheadline`
--

INSERT INTO `tagcontentheadline` (`cheadlineID`, `tagID`) VALUES
(7, 77889360);

-- --------------------------------------------------------

--
-- Table structure for table `tagcontentsubheadline`
--

DROP TABLE IF EXISTS `tagcontentsubheadline`;
CREATE TABLE IF NOT EXISTS `tagcontentsubheadline` (
  `csubheadlineID` int(11) NOT NULL,
  `tagID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tagcontentsubheadline`
--

INSERT INTO `tagcontentsubheadline` (`csubheadlineID`, `tagID`) VALUES
(2, 77889360);

-- --------------------------------------------------------

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
CREATE TABLE IF NOT EXISTS `tags` (
  `tagID` int(11) NOT NULL,
  `tagName` varchar(200) NOT NULL,
  `global` tinyint(1) NOT NULL,
  `docID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=77889361 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tags`
--

INSERT INTO `tags` (`tagID`, `tagName`, `global`, `docID`) VALUES
(59832060, 'testTag', 0, NULL),
(77889359, 'globalTag', 1, 2),
(77889360, 'otherTag', 0, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tagsubcomments`
--

DROP TABLE IF EXISTS `tagsubcomments`;
CREATE TABLE IF NOT EXISTS `tagsubcomments` (
  `tagID` int(11) NOT NULL,
  `subCID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `email` varchar(200) NOT NULL,
  `surName` varchar(200) NOT NULL,
  `firstName` varchar(200) NOT NULL,
  `role` int(11) NOT NULL,
  `password` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`email`, `surName`, `firstName`, `role`, `password`) VALUES
('admin.admin@admin.admin', 'admin', 'admin', 0, '1ffffff9223a7bffffffbd7325516fffffff069ffffffdf18ffffffb50'),
('testmail@mail.com', 'testuser', 'testuser', 2, '5f4dffffffcc3b5affffffa765ffffffd61dffffff8327ffffffdeffffffb8ffffff82ffffffcfffffff99');

-- --------------------------------------------------------

--
-- Table structure for table `usertags`
--

DROP TABLE IF EXISTS `usertags`;
CREATE TABLE IF NOT EXISTS `usertags` (
  `email` varchar(200) NOT NULL,
  `tagID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usertags`
--

INSERT INTO `usertags` (`email`, `tagID`) VALUES
('testmail@mail.com', 59832060),
('admin.admin@admin.admin', 77889359),
('admin.admin@admin.admin', 77889360);

-- --------------------------------------------------------

--
-- Table structure for table `versionchapter`
--

DROP TABLE IF EXISTS `versionchapter`;
CREATE TABLE IF NOT EXISTS `versionchapter` (
  `vchapterID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `chapterID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `changelog` text NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `versioncontent`
--

DROP TABLE IF EXISTS `versioncontent`;
CREATE TABLE IF NOT EXISTS `versioncontent` (
  `vcontentID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `contentID` int(11) NOT NULL,
  `content` text NOT NULL,
  `type` int(11) NOT NULL,
  `changelog` text NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `chapterID` int(11) DEFAULT NULL,
  `cheadlineID` int(11) DEFAULT NULL,
  `csubheadlineID` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `versioncontent`
--

INSERT INTO `versioncontent` (`vcontentID`, `docID`, `contentID`, `content`, `type`, `changelog`, `timestamp`, `sequence`, `chapterID`, `cheadlineID`, `csubheadlineID`) VALUES
(11, 2, 26, '<div style="height: 16px" class="editor" id="editorhnqbli">map:: (a -&gt; b) -&gt; [a] -&gt; [b]</div><script type="text/javascript">var editor = ace.edit("editorhnqbli"); editor.setTheme("ace/theme/chrome"); editor.getSession().setMode("ace/mode/haskell"); editor.resize(); editor.setReadOnly(true); editor.session.setNewLineMode("windows");</script>', 2, 'content changed', '2015-07-15T20:08:03.856', 1, NULL, NULL, 3);

-- --------------------------------------------------------

--
-- Table structure for table `versiondocument`
--

DROP TABLE IF EXISTS `versiondocument`;
CREATE TABLE IF NOT EXISTS `versiondocument` (
  `vdocID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `documentName` varchar(200) NOT NULL,
  `changelog` text NOT NULL,
  `timestamp` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `versionheadline`
--

DROP TABLE IF EXISTS `versionheadline`;
CREATE TABLE IF NOT EXISTS `versionheadline` (
  `vheadlineID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `cheadlineID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `changelog` text NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `chapterID` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `versionsubheadline`
--

DROP TABLE IF EXISTS `versionsubheadline`;
CREATE TABLE IF NOT EXISTS `versionsubheadline` (
  `vsubheadlineID` int(11) NOT NULL,
  `docID` int(11) NOT NULL,
  `csubheadlineID` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `changelog` text NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  `sequence` int(11) NOT NULL,
  `cheadlineID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookmarks`
--
ALTER TABLE `bookmarks`
  ADD PRIMARY KEY (`email`,`docID`),
  ADD KEY `email` (`email`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `chapter`
--
ALTER TABLE `chapter`
  ADD PRIMARY KEY (`chapterID`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`commentID`),
  ADD KEY `email` (`email`),
  ADD KEY `contentID` (`contentID`);

--
-- Indexes for table `content`
--
ALTER TABLE `content`
  ADD PRIMARY KEY (`contentID`),
  ADD KEY `chapterID` (`chapterID`),
  ADD KEY `cheadlineID` (`cheadlineID`),
  ADD KEY `csubheadlineID` (`csubheadlineID`);

--
-- Indexes for table `contentheadline`
--
ALTER TABLE `contentheadline`
  ADD PRIMARY KEY (`cheadlineID`),
  ADD KEY `chapterID` (`chapterID`);

--
-- Indexes for table `contentsubheadline`
--
ALTER TABLE `contentsubheadline`
  ADD PRIMARY KEY (`csubheadlineID`),
  ADD KEY `cheadlineID` (`cheadlineID`);

--
-- Indexes for table `documents`
--
ALTER TABLE `documents`
  ADD PRIMARY KEY (`docID`),
  ADD KEY `subjectID` (`subjectID`),
  ADD KEY `email` (`email`);

--
-- Indexes for table `faculties`
--
ALTER TABLE `faculties`
  ADD PRIMARY KEY (`facultyName`);

--
-- Indexes for table `flashcard`
--
ALTER TABLE `flashcard`
  ADD PRIMARY KEY (`flashCardID`),
  ADD KEY `tagID` (`tagID`),
  ADD KEY `email` (`email`);

--
-- Indexes for table `flashcardchapter`
--
ALTER TABLE `flashcardchapter`
  ADD PRIMARY KEY (`flashCardChapterID`),
  ADD KEY `flashCardID` (`flashCardID`);

--
-- Indexes for table `flashcardcomment`
--
ALTER TABLE `flashcardcomment`
  ADD PRIMARY KEY (`flashCardCommentID`),
  ADD KEY `flashCardID` (`flashCardID`),
  ADD KEY `flashCardContentID` (`flashCardContentID`);

--
-- Indexes for table `flashcardcontent`
--
ALTER TABLE `flashcardcontent`
  ADD PRIMARY KEY (`flashCardContentID`),
  ADD KEY `flashCardID` (`flashCardID`),
  ADD KEY `flashCardChapterID` (`flashCardChapterID`),
  ADD KEY `flashCardHeadlineID` (`flashCardHeadlineID`),
  ADD KEY `flashCardSubCommentID` (`flashCardSubHeadlineID`);

--
-- Indexes for table `flashcardheadline`
--
ALTER TABLE `flashcardheadline`
  ADD PRIMARY KEY (`flashCardHeadlineID`),
  ADD KEY `flashCardID` (`flashCardID`),
  ADD KEY `flashCardChapterID` (`flashCardChapterID`);

--
-- Indexes for table `flashcardsubcomment`
--
ALTER TABLE `flashcardsubcomment`
  ADD PRIMARY KEY (`flashCardSubCommentID`),
  ADD KEY `flashCardID` (`flashCardID`),
  ADD KEY `flashCardCommentID` (`flashCardCommentID`);

--
-- Indexes for table `flashcardsubheadline`
--
ALTER TABLE `flashcardsubheadline`
  ADD PRIMARY KEY (`flashCardSubHeadlineID`),
  ADD KEY `flashCardID` (`flashCardID`),
  ADD KEY `flashCardHeadlineID` (`flashCardHeadlineID`);

--
-- Indexes for table `moderators`
--
ALTER TABLE `moderators`
  ADD PRIMARY KEY (`email`,`docID`),
  ADD KEY `email` (`email`,`docID`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `reference`
--
ALTER TABLE `reference`
  ADD PRIMARY KEY (`referenceID`),
  ADD KEY `contentID` (`contentID`),
  ADD KEY `referencesToID` (`referencesToID`);

--
-- Indexes for table `subcomments`
--
ALTER TABLE `subcomments`
  ADD PRIMARY KEY (`subCID`),
  ADD KEY `email` (`email`),
  ADD KEY `commentID` (`commentID`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`subjectID`),
  ADD KEY `facultyID` (`facultyName`);

--
-- Indexes for table `suggestions`
--
ALTER TABLE `suggestions`
  ADD PRIMARY KEY (`suggestionID`),
  ADD KEY `parentID` (`parentID`);

--
-- Indexes for table `tagchaptersan`
--
ALTER TABLE `tagchaptersan`
  ADD PRIMARY KEY (`tagID`,`chapterID`),
  ADD KEY `chapterID` (`chapterID`);

--
-- Indexes for table `tagcomments`
--
ALTER TABLE `tagcomments`
  ADD PRIMARY KEY (`tagID`,`commentID`),
  ADD KEY `tagID` (`tagID`,`commentID`),
  ADD KEY `commentID` (`commentID`);

--
-- Indexes for table `tagcontentheadline`
--
ALTER TABLE `tagcontentheadline`
  ADD PRIMARY KEY (`cheadlineID`,`tagID`),
  ADD KEY `tagID` (`tagID`);

--
-- Indexes for table `tagcontentsubheadline`
--
ALTER TABLE `tagcontentsubheadline`
  ADD PRIMARY KEY (`csubheadlineID`,`tagID`),
  ADD KEY `tagID` (`tagID`);

--
-- Indexes for table `tags`
--
ALTER TABLE `tags`
  ADD PRIMARY KEY (`tagID`);

--
-- Indexes for table `tagsubcomments`
--
ALTER TABLE `tagsubcomments`
  ADD PRIMARY KEY (`tagID`,`subCID`),
  ADD KEY `tagID` (`tagID`,`subCID`),
  ADD KEY `subCID` (`subCID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `usertags`
--
ALTER TABLE `usertags`
  ADD PRIMARY KEY (`email`,`tagID`),
  ADD KEY `email` (`email`,`tagID`),
  ADD KEY `tagID` (`tagID`);

--
-- Indexes for table `versionchapter`
--
ALTER TABLE `versionchapter`
  ADD PRIMARY KEY (`vchapterID`),
  ADD KEY `docID` (`docID`,`chapterID`);

--
-- Indexes for table `versioncontent`
--
ALTER TABLE `versioncontent`
  ADD PRIMARY KEY (`vcontentID`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `versiondocument`
--
ALTER TABLE `versiondocument`
  ADD PRIMARY KEY (`vdocID`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `versionheadline`
--
ALTER TABLE `versionheadline`
  ADD PRIMARY KEY (`vheadlineID`),
  ADD KEY `docID` (`docID`);

--
-- Indexes for table `versionsubheadline`
--
ALTER TABLE `versionsubheadline`
  ADD PRIMARY KEY (`vsubheadlineID`),
  ADD KEY `docID` (`docID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chapter`
--
ALTER TABLE `chapter`
  MODIFY `chapterID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `commentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `content`
--
ALTER TABLE `content`
  MODIFY `contentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=29;
--
-- AUTO_INCREMENT for table `contentheadline`
--
ALTER TABLE `contentheadline`
  MODIFY `cheadlineID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `contentsubheadline`
--
ALTER TABLE `contentsubheadline`
  MODIFY `csubheadlineID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `documents`
--
ALTER TABLE `documents`
  MODIFY `docID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `flashcard`
--
ALTER TABLE `flashcard`
  MODIFY `flashCardID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6418900;
--
-- AUTO_INCREMENT for table `flashcardchapter`
--
ALTER TABLE `flashcardchapter`
  MODIFY `flashCardChapterID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9984769;
--
-- AUTO_INCREMENT for table `flashcardcomment`
--
ALTER TABLE `flashcardcomment`
  MODIFY `flashCardCommentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `flashcardcontent`
--
ALTER TABLE `flashcardcontent`
  MODIFY `flashCardContentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=36;
--
-- AUTO_INCREMENT for table `flashcardheadline`
--
ALTER TABLE `flashcardheadline`
  MODIFY `flashCardHeadlineID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `flashcardsubcomment`
--
ALTER TABLE `flashcardsubcomment`
  MODIFY `flashCardSubCommentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `flashcardsubheadline`
--
ALTER TABLE `flashcardsubheadline`
  MODIFY `flashCardSubHeadlineID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `reference`
--
ALTER TABLE `reference`
  MODIFY `referenceID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `subcomments`
--
ALTER TABLE `subcomments`
  MODIFY `subCID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `subjectID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `suggestions`
--
ALTER TABLE `suggestions`
  MODIFY `suggestionID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `tags`
--
ALTER TABLE `tags`
  MODIFY `tagID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=77889361;
--
-- AUTO_INCREMENT for table `versionchapter`
--
ALTER TABLE `versionchapter`
  MODIFY `vchapterID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `versioncontent`
--
ALTER TABLE `versioncontent`
  MODIFY `vcontentID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `versiondocument`
--
ALTER TABLE `versiondocument`
  MODIFY `vdocID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `versionheadline`
--
ALTER TABLE `versionheadline`
  MODIFY `vheadlineID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `versionsubheadline`
--
ALTER TABLE `versionsubheadline`
  MODIFY `vsubheadlineID` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookmarks`
--
ALTER TABLE `bookmarks`
  ADD CONSTRAINT `bookmarks_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bookmarks_ibfk_2` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `chapter`
--
ALTER TABLE `chapter`
  ADD CONSTRAINT `chapter_ibfk_1` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`contentID`) REFERENCES `content` (`contentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `content`
--
ALTER TABLE `content`
  ADD CONSTRAINT `content_ibfk_2` FOREIGN KEY (`cheadlineID`) REFERENCES `contentheadline` (`cheadlineID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `content_ibfk_3` FOREIGN KEY (`csubheadlineID`) REFERENCES `contentsubheadline` (`csubheadlineID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `content_ibfk_4` FOREIGN KEY (`chapterID`) REFERENCES `chapter` (`chapterID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `contentheadline`
--
ALTER TABLE `contentheadline`
  ADD CONSTRAINT `contentheadline_ibfk_1` FOREIGN KEY (`chapterID`) REFERENCES `chapter` (`chapterID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `contentsubheadline`
--
ALTER TABLE `contentsubheadline`
  ADD CONSTRAINT `contentsubheadline_ibfk_1` FOREIGN KEY (`cheadlineID`) REFERENCES `contentheadline` (`cheadlineID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`subjectID`) REFERENCES `subjects` (`subjectID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `documents_ibfk_2` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `flashcard`
--
ALTER TABLE `flashcard`
  ADD CONSTRAINT `flashcard_ibfk_2` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardchapter`
--
ALTER TABLE `flashcardchapter`
  ADD CONSTRAINT `flashcardchapter_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardcomment`
--
ALTER TABLE `flashcardcomment`
  ADD CONSTRAINT `flashcardcomment_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardcomment_ibfk_2` FOREIGN KEY (`flashCardContentID`) REFERENCES `flashcardcontent` (`flashCardContentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardcontent`
--
ALTER TABLE `flashcardcontent`
  ADD CONSTRAINT `flashcardcontent_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardcontent_ibfk_2` FOREIGN KEY (`flashCardChapterID`) REFERENCES `flashcardchapter` (`flashCardChapterID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardcontent_ibfk_3` FOREIGN KEY (`flashCardHeadlineID`) REFERENCES `flashcardheadline` (`flashCardHeadlineID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardcontent_ibfk_4` FOREIGN KEY (`flashCardSubHeadlineID`) REFERENCES `flashcardsubheadline` (`flashCardSubHeadlineID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardheadline`
--
ALTER TABLE `flashcardheadline`
  ADD CONSTRAINT `flashcardheadline_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardheadline_ibfk_2` FOREIGN KEY (`flashCardChapterID`) REFERENCES `flashcardchapter` (`flashCardChapterID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardsubcomment`
--
ALTER TABLE `flashcardsubcomment`
  ADD CONSTRAINT `flashcardsubcomment_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardsubcomment_ibfk_2` FOREIGN KEY (`flashCardCommentID`) REFERENCES `flashcardcomment` (`flashCardCommentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `flashcardsubheadline`
--
ALTER TABLE `flashcardsubheadline`
  ADD CONSTRAINT `flashcardsubheadline_ibfk_1` FOREIGN KEY (`flashCardID`) REFERENCES `flashcard` (`flashCardID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `flashcardsubheadline_ibfk_2` FOREIGN KEY (`flashCardHeadlineID`) REFERENCES `flashcardheadline` (`flashCardHeadlineID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `moderators`
--
ALTER TABLE `moderators`
  ADD CONSTRAINT `moderators_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `moderators_ibfk_2` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reference`
--
ALTER TABLE `reference`
  ADD CONSTRAINT `reference_ibfk_1` FOREIGN KEY (`contentID`) REFERENCES `content` (`contentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `reference_ibfk_2` FOREIGN KEY (`referencesToID`) REFERENCES `content` (`contentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `subcomments`
--
ALTER TABLE `subcomments`
  ADD CONSTRAINT `subcomments_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `subcomments_ibfk_2` FOREIGN KEY (`commentID`) REFERENCES `comments` (`commentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `subjects`
--
ALTER TABLE `subjects`
  ADD CONSTRAINT `subjects_ibfk_1` FOREIGN KEY (`facultyName`) REFERENCES `faculties` (`facultyName`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tagchaptersan`
--
ALTER TABLE `tagchaptersan`
  ADD CONSTRAINT `tagchaptersan_ibfk_1` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tagchaptersan_ibfk_2` FOREIGN KEY (`chapterID`) REFERENCES `chapter` (`chapterID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tagcomments`
--
ALTER TABLE `tagcomments`
  ADD CONSTRAINT `tagcomments_ibfk_1` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tagcomments_ibfk_2` FOREIGN KEY (`commentID`) REFERENCES `comments` (`commentID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tagcontentheadline`
--
ALTER TABLE `tagcontentheadline`
  ADD CONSTRAINT `tagcontentheadline_ibfk_1` FOREIGN KEY (`cheadlineID`) REFERENCES `contentheadline` (`cheadlineID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tagcontentheadline_ibfk_2` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tagcontentsubheadline`
--
ALTER TABLE `tagcontentsubheadline`
  ADD CONSTRAINT `tagcontentsubheadline_ibfk_1` FOREIGN KEY (`csubheadlineID`) REFERENCES `contentsubheadline` (`csubheadlineID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tagcontentsubheadline_ibfk_2` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tagsubcomments`
--
ALTER TABLE `tagsubcomments`
  ADD CONSTRAINT `tagsubcomments_ibfk_1` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tagsubcomments_ibfk_2` FOREIGN KEY (`subCID`) REFERENCES `subcomments` (`subCID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `usertags`
--
ALTER TABLE `usertags`
  ADD CONSTRAINT `usertags_ibfk_1` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `usertags_ibfk_2` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tagID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `versionchapter`
--
ALTER TABLE `versionchapter`
  ADD CONSTRAINT `versionchapter_ibfk_1` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `versioncontent`
--
ALTER TABLE `versioncontent`
  ADD CONSTRAINT `versioncontent_ibfk_1` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `versionheadline`
--
ALTER TABLE `versionheadline`
  ADD CONSTRAINT `versionheadline_ibfk_1` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `versionsubheadline`
--
ALTER TABLE `versionsubheadline`
  ADD CONSTRAINT `versionsubheadline_ibfk_1` FOREIGN KEY (`docID`) REFERENCES `documents` (`docID`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
