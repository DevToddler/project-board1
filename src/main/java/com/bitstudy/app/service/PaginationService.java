package com.bitstudy.app.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/** 할일 : 게시판 페이지 페이지네이션 서비스 구현 */
@Service
public class PaginationService {
	/** 한번에 몇 페이지 보여줄지*/
	private static final int BAR_LENGTH = 5;

	public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages){
		/** startNumber, endNumber 구하기
		 	현재 선택한 페이지 번호가 페이징 부분의 가운데 위치하도록 만들것이다. */
		int startNumber = Math.max(0, currentPageNumber - (BAR_LENGTH / 2));
		int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

		return IntStream.range(startNumber, endNumber).boxed().toList();
	}

	public int currentBarLength(){
		return BAR_LENGTH;
	}

}
